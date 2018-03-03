package tz.co.nezatech.apps.survey.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import tz.co.nezatech.apps.survey.model.Role;
import tz.co.nezatech.apps.util.nezadb.model.Status;
import tz.co.nezatech.apps.util.nezadb.repository.BaseDataRepository;

@Repository
public class RoleRepository extends BaseDataRepository<Role> {
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public RowMapper<Role> getRowMapper() {
		return new RowMapper<Role>() {

			@Override
			public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
				Role e = new Role(rs.getString("name"), rs.getString("description"));
				e.setId(rs.getLong("id"));
				return e;
			}
		};
	}

	@Override
	public String sqlFindAll() {
		return "select r.* from tbl_role r where 1=1 ";
	}

	@Override
	public String sqlFindById() {
		return sqlFindAll() + " and id = ? ";
	}

	@Override
	public PreparedStatement psCreate(Role entity, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("insert into tbl_role(name, description) values (?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, entity.getName());
			ps.setString(2, entity.getDescription());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	@Override
	public PreparedStatement psUpdate(Role entity, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("update tbl_role set name=?,  description=? where id=?");
			ps.setString(1, entity.getName());
			ps.setString(2, entity.getDescription());
			ps.setLong(3, entity.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	@Override
	public PreparedStatement psDelete(long id, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("delete from tbl_role where id=?");
			ps.setLong(1, id);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	@Override
	public PreparedStatement psDeleteLinked(long id, Connection conn) {
		return null;
	}

	@Override
	public JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}

	@Override
	public Status onSave(Role entity, Status status) {
		return status;
	}

	@Override
	public PreparedStatement searchCriteria(String value, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(
					this.sqlFindAll() + " and name like ? or description like ? " + this.getOrderBy());
			for (int i = 1; i <= 2; i++) {
				ps.setString(i, "%" + value + "%");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	public Status manageMatrix(final Role e) {
		try {
			final List<Long> inserts = new ArrayList<Long>();
			final List<Long> deletes = new ArrayList<Long>();
			for (Iterator<String> iterator = e.getMtrxPermissionIds().iterator(); iterator.hasNext();) {
				String line = iterator.next();
				String tokens[] = line.split("-");
				Long permissionId = Long.parseLong(tokens[0]);
				boolean wasEnabled = Boolean.parseBoolean(tokens[1]);

				if (wasEnabled) {
					if (!e.getPermissionIds().contains(tokens[0])) {
						deletes.add(permissionId);
					}
				} else {
					if (e.getPermissionIds().contains(tokens[0])) {
						inserts.add(permissionId);
					}
				}
			}

			// Inserts
			matrixInserts(inserts, e);
			
			// Deletes
			matrixDeletes(deletes, e);
			return new Status(200, "Successfully created enity " + e.getName(), 0);
		} catch (Exception e2) {
			e2.printStackTrace();
			return new Status(500, "Role vs Permission matrix update failed. Error message: " + e2.getMessage(), 0);
		}
	}

	public void matrixInserts(final List<Long> inserts, final Role e) {
		// Inserts
		getJdbcTemplate().batchUpdate(
				"INSERT INTO tbl_role_permission (permission_id, role_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE permission_id=permission_id ",
				new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						Long permissionId = inserts.get(i);
						ps.setLong(1, permissionId);
						ps.setLong(2, e.getId());
					}

					@Override
					public int getBatchSize() {
						return inserts.size();
					}
				});
	}

	public void matrixDeletes(final List<Long> deletes, final Role e) {
		// Deletes
		getJdbcTemplate().batchUpdate("DELETE FROM tbl_role_permission where permission_id=? and role_id=? ",
				new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						Long permissionId = deletes.get(i);
						ps.setLong(1, permissionId);
						ps.setLong(2, e.getId());
					}

					@Override
					public int getBatchSize() {
						return deletes.size();
					}
				});
	}
}

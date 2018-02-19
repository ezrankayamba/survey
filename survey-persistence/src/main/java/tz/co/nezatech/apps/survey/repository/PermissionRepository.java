package tz.co.nezatech.apps.survey.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import tz.co.nezatech.apps.survey.model.Permission;
import tz.co.nezatech.apps.util.nezadb.model.Status;
import tz.co.nezatech.apps.util.nezadb.repository.BaseDataRepository;

@Repository
public class PermissionRepository extends BaseDataRepository<Permission> {
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public RowMapper<Permission> getRowMapper() {
		return new RowMapper<Permission>() {

			@Override
			public Permission mapRow(ResultSet rs, int rowNum) throws SQLException {
				Permission e = new Permission(rs.getString("name"), rs.getString("description"));
				e.setId(rs.getLong("id"));
				Long parentId = rs.getLong("parent");
				if (parentId != null && parentId > 0) {
					Permission parent = new Permission(rs.getString("parent_name"), rs.getString("parent_description"));
					parent.setId(rs.getLong("parent"));

					e.setParent(parent);
				}
				e.setEnabled(rs.getBoolean("enabled"));
				return e;
			}
		};
	}

	@Override
	public String sqlFindAll() {
		return "select p.*, prt.name as parent_name, prt.description as parent_description, 0 as enabled from tbl_permission p left join tbl_permission prt on p.parent=prt.id where 1=1 ";
	}

	@Override
	public String sqlFindById() {
		return sqlFindAll() + " and p.id = ? ";
	}

	@Override
	public PreparedStatement psCreate(Permission entity, Connection conn) {
		PreparedStatement ps = null;
		try {
			Permission prt = entity.getParent();
			Long parentId = null;
			if (prt != null) {
				parentId = prt.getId();
			}
			ps = conn.prepareStatement("insert into tbl_permission(name, description, parent) values (?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, entity.getName());
			ps.setString(2, entity.getDescription());
			ps.setObject(3, parentId);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	@Override
	public PreparedStatement psUpdate(Permission entity, Connection conn) {
		PreparedStatement ps = null;
		try {
			Permission prt = entity.getParent();
			Long parentId = null;
			if (prt != null) {
				parentId = prt.getId();
			}
			ps = conn.prepareStatement("update tbl_permission set name=?, description=?, parent=? where id=?");
			ps.setString(1, entity.getName());
			ps.setString(2, entity.getDescription());
			ps.setObject(3, parentId);
			ps.setLong(4, entity.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	@Override
	public PreparedStatement psDelete(long id, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("delete from tbl_permission where id=?");
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
	public Status onSave(Permission entity, Status status) {
		return status;
	}

	@Override
	public PreparedStatement searchCriteria(String value, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(
					this.sqlFindAll() + " and p.name like ? or p.description like ? " + this.getOrderBy());
			for (int i = 1; i <= 2; i++) {
				ps.setString(i, "%" + value + "%");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	private String matrixSql() {
		String sql = "select p.*, prt.name as parent_name, prt.description as parent_description, IF(rp.role_id is null, 0, 1) as enabled from tbl_permission p left join tbl_permission prt on p.parent=prt.id left outer join tbl_role_permission rp on p.id=rp.permission_id and (rp.role_id is null or rp.role_id = ?) where 1=1 ";
		return sql;
	}

	private PreparedStatement matrixPstm(Connection conn, Long roleId) {

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(matrixSql() + (getOrderBy() == null ? "" : getOrderBy()));
			ps.setLong(1, roleId);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	private PreparedStatement matrixPstmSearch(Connection conn, Long roleId, String value) {

		PreparedStatement ps = null;
		try {
			String sql = matrixSql() + " and (p.name like ? or p.description like ?) "
					+ (getOrderBy() == null ? "" : getOrderBy());
			System.out.println("SQL: " + sql);
			ps = conn.prepareStatement(sql);
			ps.setLong(1, roleId);
			for (int i = 2; i <= 3; i++) {
				ps.setString(i, "%" + value + "%");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	public List<Permission> matrix(final Long roleId) {

		return getJdbcTemplate().query(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				return matrixPstm(conn, roleId);
			}
		}, getRowMapper());
	}

	public List<Permission> matrixSearch(final Long roleId, final String value) {

		return getJdbcTemplate().query(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				return matrixPstmSearch(conn, roleId, value);
			}
		}, getRowMapper());
	}

}

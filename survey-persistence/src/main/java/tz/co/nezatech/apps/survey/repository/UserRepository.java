package tz.co.nezatech.apps.survey.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import tz.co.nezatech.apps.survey.model.Role;
import tz.co.nezatech.apps.survey.model.User;
import tz.co.nezatech.apps.util.nezadb.model.Status;
import tz.co.nezatech.apps.util.nezadb.repository.BaseDataRepository;

@Repository
public class UserRepository extends BaseDataRepository<User> {
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	PermissionRepository permissionRepository;

	@Override
	public RowMapper<User> getRowMapper() {
		return new RowMapper<User>() {

			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User e = new User(rs.getString("username"), rs.getString("password"), rs.getString("email"),
						rs.getString("full_name"));
				Role r = new Role(rs.getString("role_name"), rs.getString("role_description"));
				r.setId(rs.getLong("role_id"));
				r.setPermissions(permissionRepository.matrix(r.getId()));
				e.setRole(r);
				e.setId(rs.getLong("id"));
				e.setResetOn(rs.getBoolean("reset_on"));
				e.setEnabled(rs.getBoolean("enabled"));

				return e;
			}
		};
	}

	@Override
	public String sqlFindAll() {
		return "select u.*, r.name as role_name, r.description as role_description from tbl_user u left join tbl_role r on u.role_id=r.id where 1=1 ";
	}

	@Override
	public String sqlFindById() {
		return sqlFindAll() + " and u.id = ? ";
	}

	@Override
	public PreparedStatement psCreate(User entity, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(
					"insert into tbl_user(username, password, email, enabled, role_id, full_name) values (?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, entity.getUsername());
			ps.setString(2, entity.getPassword());
			ps.setString(3, entity.getEmail());
			ps.setBoolean(4, true);
			ps.setObject(5, entity.getRole() != null ? entity.getRole().getId() : null);
			ps.setString(6, entity.getFullName());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	@Override
	public PreparedStatement psUpdate(User entity, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(
					"update tbl_user set username=?,  password=?, email=?, enabled=?, role_id=?, full_name=? where id=?");
			ps.setString(1, entity.getUsername());
			ps.setString(2, entity.getPassword());
			ps.setString(3, entity.getEmail());
			ps.setBoolean(4, true);
			ps.setLong(5, entity.getRole().getId());
			ps.setString(6, entity.getFullName());
			ps.setLong(7, entity.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	@Override
	public PreparedStatement psDelete(long id, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("delete from tbl_user where id=?");
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
	public Status onSave(User entity, Status status) {
		return status;
	}

	@Override
	public PreparedStatement searchCriteria(String value, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(
					this.sqlFindAll() + " and u.username like ? or u.email like ? " + this.getOrderBy());
			for (int i = 1; i <= 2; i++) {
				ps.setString(i, "%" + value + "%");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	public Status updateChangePwd(final String newPassword, final Long id) {
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn
							.prepareStatement("update tbl_user set reset_on = 0, password=? where id=? ");
					ps.setString(1, newPassword);
					ps.setLong(2, id);
					return ps;
				}
			});
			return new Status(200, "Successfully created user id " + id, 0);
		} catch (Exception e) {
			e.printStackTrace();
			return new Status(500, "User password update failed. Error message: " + e.getMessage(), 0);
		}
	}

	public void resetPwd(final User e) {
		getJdbcTemplate().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement ps = conn
						.prepareStatement("update tbl_user set reset_on = 1, password=? where id=? ");
				ps.setString(1, e.getPassword());
				ps.setLong(2, e.getId());
				return ps;
			}
		});
	}
}

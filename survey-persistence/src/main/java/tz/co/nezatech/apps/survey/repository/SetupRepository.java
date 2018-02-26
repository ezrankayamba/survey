package tz.co.nezatech.apps.survey.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import tz.co.nezatech.apps.survey.model.Setup;
import tz.co.nezatech.apps.util.nezadb.model.Status;
import tz.co.nezatech.apps.util.nezadb.repository.BaseDataRepository;

@Repository
public class SetupRepository extends BaseDataRepository<Setup> {
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public RowMapper<Setup> getRowMapper() {
		return new RowMapper<Setup>() {

			@Override
			public Setup mapRow(ResultSet rs, int rowNum) throws SQLException {
				Setup e = new Setup(rs.getString("uuid"), rs.getString("name"), rs.getString("type"));
				return e;
			}
		};
	}

	@Override
	public String sqlFindAll() {
		return "select s.* from vw_data_setup s where 1=1 ";
	}

	@Override
	public String sqlFindById() {
		return sqlFindAll() + " and s.id = ? ";
	}

	@Override
	public PreparedStatement psCreate(Setup entity, Connection conn) {
		PreparedStatement ps = null;
		return ps;
	}

	@Override
	public PreparedStatement psUpdate(Setup entity, Connection conn) {
		PreparedStatement ps = null;
		return ps;
	}

	@Override
	public PreparedStatement psDelete(long id, Connection conn) {
		PreparedStatement ps = null;
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
	public Status onSave(Setup entity, Status status) {
		return status;
	}

	@Override
	public PreparedStatement searchCriteria(String value, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(
					this.sqlFindAll() + " and s.type = ? and s.last_update >= ? " + this.getOrderBy());
			String []tokens = value.split("/");
			for (int i = 1; i <= 2; i++) {
				ps.setString(i, tokens[i-1]);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}
}

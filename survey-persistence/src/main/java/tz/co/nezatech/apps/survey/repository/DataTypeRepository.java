package tz.co.nezatech.apps.survey.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import tz.co.nezatech.apps.survey.model.DataType;
import tz.co.nezatech.apps.util.nezadb.model.Status;
import tz.co.nezatech.apps.util.nezadb.repository.BaseDataRepository;

@Repository
public class DataTypeRepository extends BaseDataRepository<DataType> {
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	DateFormat df =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public RowMapper<DataType> getRowMapper() {
		return new RowMapper<DataType>() {

			@Override
			public DataType mapRow(ResultSet rs, int rowNum) throws SQLException {
				DataType e = new DataType(rs.getString("name"), rs.getString("type"), df.format(fromSQLTimestamp(rs.getTimestamp("last_update"))));
				return e;
			}
		};
	}

	@Override
	public String sqlFindAll() {
		return "select dt.* from tbl_data_type dt where 1=1 ";
	}

	@Override
	public String sqlFindById() {
		return sqlFindAll() + " and dt.id = ? ";
	}

	@Override
	public PreparedStatement psCreate(DataType entity, Connection conn) {
		PreparedStatement ps = null;
		return ps;
	}

	@Override
	public PreparedStatement psUpdate(DataType entity, Connection conn) {
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
	public Status onSave(DataType entity, Status status) {
		return status;
	}

	@Override
	public PreparedStatement searchCriteria(String value, Connection conn) {
		PreparedStatement ps = null;
		try {
			
			if(value.contains("/")) {
				String []tokens = value.split("/");
				ps = conn.prepareStatement(
						this.sqlFindAll() + " and dt.type = ? and dt.last_update > ? " + this.getOrderBy());
				
				for (int i = 1; i <= 2; i++) {
					ps.setString(i, tokens[i-1]);
				}
			}else {
				ps = conn.prepareStatement(
						this.sqlFindAll() + " and dt.last_update > ? " + this.getOrderBy());
				for (int i = 1; i <= 1; i++) {
					ps.setString(i, value);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}
}

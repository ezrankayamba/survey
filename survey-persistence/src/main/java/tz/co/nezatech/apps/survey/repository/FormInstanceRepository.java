package tz.co.nezatech.apps.survey.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import tz.co.nezatech.apps.survey.model.FormInstance;
import tz.co.nezatech.apps.util.nezadb.model.Status;
import tz.co.nezatech.apps.util.nezadb.repository.BaseDataRepository;

@Repository
public class FormInstanceRepository extends BaseDataRepository<FormInstance> {
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	FormRepository formRepository;
	@Autowired
	UserRepository userRepository;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public RowMapper<FormInstance> getRowMapper() {

		return new RowMapper<FormInstance>() {

			@Override
			public FormInstance mapRow(ResultSet rs, int rowNum) throws SQLException {
				FormInstance e = new FormInstance(rs.getString("name"), formRepository.findById(rs.getLong("form_id")),
						rs.getString("json"), rs.getString("uuid"), rs.getInt("status"),
						userRepository.findById(rs.getLong("recorded_by")));
				e.setLastUpdate(fromSQLTimestamp(rs.getTimestamp("last_update")));
				e.setRecordDate(fromSQLTimestamp(rs.getTimestamp("record_date")));
				e.setId(rs.getLong("id"));
				return e;
			}
		};
	}

	@Override
	public String sqlFindAll() {
		return "select fi.* from tbl_form_instance fi where 1=1 ";
	}

	@Override
	public String sqlFindById() {
		return sqlFindAll() + " and fi.id = ? ";
	}

	@Override
	public PreparedStatement psCreate(FormInstance entity, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(
					"insert into tbl_form_instance(uuid, name, record_date, status, json, form_id, recorded_by) values (?, ?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, entity.getUuid());
			ps.setString(2, entity.getName());
			ps.setTimestamp(3, toSQLTimestamp(entity.getRecordDate()));
			ps.setInt(4, entity.getStatus());
			// ps.setTimestamp(5, toSQLTimestamp(entity.getRecordDate()));
			ps.setString(5, entity.getJson());
			ps.setLong(6, entity.getForm().getId());
			ps.setLong(7, entity.getRecordedBy().getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	@Override
	public PreparedStatement psUpdate(FormInstance entity, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(
					"update tbl_form_instance set uuid=?, name=?, record_date=?, status=?, json=?, form_id=?, recorded_by=? where id=?");
			ps.setString(1, entity.getUuid());
			ps.setString(2, entity.getName());
			ps.setTimestamp(3, toSQLTimestamp(entity.getRecordDate()));
			ps.setInt(4, entity.getStatus());
			// ps.setTimestamp(5, toSQLTimestamp(entity.getRecordDate()));
			ps.setString(5, entity.getJson());
			ps.setLong(6, entity.getForm().getId());
			ps.setLong(7, entity.getRecordedBy().getId());
			ps.setLong(8, entity.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	@Override
	public PreparedStatement psDelete(long id, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("delete from tbl_form_instance where id=?");
			ps.setLong(1, id);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	@Override
	public JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}

	@Override
	public Status onSave(FormInstance entity, Status status) {
		return status;
	}

	@Override
	public PreparedStatement searchCriteria(String value, Connection conn) {
		PreparedStatement ps = null;
		try {
			long formId = 0;
			String search = null;
			String tokens[] = value.split(":");
			if (tokens.length > 1) {
				formId = Integer.parseInt(tokens[0]);
				search = tokens[1];
			} else {
				search = value;
			}
			if (formId == 0) {
				ps = conn.prepareStatement(this.sqlFindAll()
						+ " and (fi.name like ? or fi.uuid like ? or fi.json like ?) " + this.getOrderBy());
				for (int i = 1; i <= 3; i++) {
					ps.setString(i, "%" + search + "%");
				}
			} else {
				ps = conn.prepareStatement(
						this.sqlFindAll() + " and (fi.name like ? or fi.uuid like ? or fi.json like ?) and form_id = ? "
								+ this.getOrderBy());
				for (int i = 1; i <= 3; i++) {
					ps.setString(i, "%" + search + "%");
				}
				ps.setLong(4, formId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	@Override
	public PreparedStatement searchCriteria(Map<String, Object> searchFileters, Connection conn) {
		PreparedStatement ps = null;
		try {
			logger.debug("searchCriteria:Params: " + searchFileters);
			final Filter f = new Filter();
			searchFileters.forEach((key, value) -> {
				String op = "=";
				if (value instanceof String) {
					String[] tokens = value.toString().split(",");
					logger.debug(Arrays.asList(tokens).toString());
					if (tokens.length == 2) {
						op = tokens[0];
					}
				}
				f.sql += String.format(" and fi.%s %s ?", key, op);
			});
			f.sql += this.getOrderBy();
			String sql = this.sqlFindAll() + f.sql;
			logger.debug("SQL: " + sql);
			final PreparedStatement tmp = conn.prepareStatement(sql);

			searchFileters.forEach((key, value) -> {
				f.count++;
				try {
					String v = "";
					if (value instanceof String) {
						String[] tokens = value.toString().split(",");
						if (tokens.length == 2) {
							v = tokens[1];
						} else {
							v = value.toString();
						}
					}
					tmp.setObject(f.count, v);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.error("Error: " + e.getMessage());
				}
			});

			ps = tmp;

		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Error: " + e.getMessage());
		}

		return ps;
	}

	@Override
	public PreparedStatement psDeleteLinked(long id, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("delete from tbl_form_instance where form_id=?");
			ps.setLong(1, id);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	class Filter {
		String sql = "";
		int count = 0;
	}

}

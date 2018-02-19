package tz.co.nezatech.apps.survey.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import tz.co.nezatech.apps.survey.model.Form;
import tz.co.nezatech.apps.util.nezadb.model.Status;
import tz.co.nezatech.apps.util.nezadb.repository.BaseDataRepository;

@Repository
public class FormRepository extends BaseDataRepository<Form> {
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public RowMapper<Form> getRowMapper() {
		return new RowMapper<Form>() {

			@Override
			public Form mapRow(ResultSet rs, int rowNum) throws SQLException {
				Form e = new Form(rs.getString("name"), rs.getString("description"));
				e.setId(rs.getLong("id"));
				e.setJson(rs.getString("json"));
				e.setDisplay(rs.getString("display"));
				e.setCount(countInstance(e.getId()));
				return e;
			}
		};
	}

	@Override
	public String sqlFindAll() {
		return "select f.* from tbl_form f where 1=1 ";
	}

	@Override
	public String sqlFindById() {
		return sqlFindAll() + " and id = ? ";
	}

	@Override
	public PreparedStatement psCreate(Form entity, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(
					"insert into tbl_form(name, description, filepath, json, display) values (?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, entity.getName());
			ps.setString(2, entity.getDescription());
			ps.setString(3, entity.getFilepath());
			ps.setString(4, entity.getJson());
			ps.setString(5, entity.getDisplay());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	@Override
	public PreparedStatement psUpdate(Form entity, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(
					"update tbl_form set name=?,  description=?, filepath=?, json=?, display=? where id=?");
			ps.setString(1, entity.getName());
			ps.setString(2, entity.getDescription());
			ps.setString(3, entity.getFilepath());
			ps.setString(4, entity.getJson());
			ps.setString(5, entity.getDisplay());
			ps.setLong(6, entity.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	@Override
	public PreparedStatement psDelete(long id, Connection conn) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("delete from tbl_form where id=?");
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
	public Status onSave(Form entity, Status status) {
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

	public int countInstance(final long reposId) {
		int count = 0;
		String sql = "select count(*) as count, form_id from tbl_form_instance fi  where fi.form_id = ? group by fi.form_id";
		List<Summary> res=getJdbcTemplate().query(sql, new Object[] { reposId }, new RowMapper<Summary>() {

			@Override
			public Summary mapRow(ResultSet rs, int arg1) throws SQLException {
				Summary summary = new Summary(reposId, rs.getInt("count"));
				return summary;
			}

		});
		if(res!=null && !res.isEmpty()) {
			count=res.get(0).getCount();
		}
		return count;
	}

	class Summary {
		long formId;
		int count;

		public Summary(long formId, int count) {
			super();
			this.formId = formId;
			this.count = count;
		}

		public long getFormId() {
			return formId;
		}

		public void setFormId(long formId) {
			this.formId = formId;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

	}
}

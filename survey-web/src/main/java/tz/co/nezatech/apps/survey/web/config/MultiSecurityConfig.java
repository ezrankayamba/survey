package tz.co.nezatech.apps.survey.web.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MultiSecurityConfig {
	@Autowired
	DataSource dataSource;
	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Autowired
	public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
				.usersByUsernameQuery("select username,password, enabled from tbl_user where username=?")
				.authoritiesByUsernameQuery(
						"select * from " + "(select u.username, p.name as authority from tbl_user u "
								+ "left join tbl_role r on u.role_id=r.id "
								+ "left join tbl_role_permission rp on r.id=rp.role_id "
								+ "left join tbl_permission p on rp.permission_id=p.id " + "UNION "
								+ "select u.username, CONCAT('ROLE_',r.name) as authority from tbl_user u "
								+ "left join tbl_role r on u.role_id=r.id) as authorities " + "where username=?")
				.passwordEncoder(new BCryptPasswordEncoder());
	}

	@Configuration
	@Order(1)
	public static class ApiSecurityConfig extends WebSecurityConfigurerAdapter {
		protected void configure(HttpSecurity http) throws Exception {
			http.antMatcher("/api/**").authorizeRequests().anyRequest().authenticated().and().httpBasic();
			http.csrf().disable();
		}
	}

	@Configuration
	public static class FormSecurityConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().antMatchers("/", "/css/**", "/js/**", "/img/**", "/fonts/**", "/libs/**").permitAll()
					.antMatchers("/", "/home", "/pwd/verify/**").permitAll().anyRequest().authenticated().and()
					.formLogin().loginPage("/login").successForwardUrl("/home").failureUrl("/login?error=true")
					.permitAll().and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
					.logoutSuccessUrl("/login?logout=true").permitAll();

			http.exceptionHandling().accessDeniedPage("/403");
		}
	}
}

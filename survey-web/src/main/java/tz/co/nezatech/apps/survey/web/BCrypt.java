package tz.co.nezatech.apps.survey.web;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class BCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pwd = encoder.encode("123456");
        System.out.println("Pwd: " + pwd);
        String sql = "select * from " + "(select u.username, p.name as authority from tbl_user u "
                + "left join tbl_role r on u.role_id=r.id " + "left join tbl_role_permission rp on r.id=rp.role_id "
                + "left join tbl_permission p on rp.permission_id=p.id " + "UNION "
                + "select u.username, CONCAT('ROLE_',r.name) as authority from tbl_user u "
                + "left join tbl_role r on u.role_id=r.id) as authorities " + "where username=?";
        // System.out.println("SQL: " + sql);
    }
}
package tz.co.nezatech.apps.survey.web;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import tz.co.nezatech.apps.survey.model.Permission;
import tz.co.nezatech.apps.survey.model.Role;
import tz.co.nezatech.apps.survey.repository.PermissionRepository;
import tz.co.nezatech.apps.survey.repository.RoleRepository;
import tz.co.nezatech.apps.util.nezadb.model.Status;

@Component
public class InitHandler implements ApplicationListener<ApplicationReadyEvent> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	PermissionRepository permissionRepository;
	@Autowired
	RoleRepository roleRepository;

	static final String ROOT_ROLE_NAME = "Root";

	public InitHandler() {
		super();
	}

	void initPermissions() {
		logger.debug("Initiatizing permissions");

		Reflections ref = new Reflections("tz.co.nezatech.apps.survey.web.controller");

		String hasAuthRegex = "^hasAnyAuthority\\('([a-zA-Z][ a-zA-Z0-9]+)'\\)$";
		Pattern pattern = Pattern.compile(hasAuthRegex);

		try {
			List<Long> inserts = new LinkedList<>();
			for (Class<?> cl : ref.getTypesAnnotatedWith(PreAuthorize.class)) {
				logger.debug("Controller: " + cl.getSimpleName());
				PreAuthorize clsPreAuth = cl.getAnnotation(PreAuthorize.class);
				String strClsAuth = clsPreAuth.value();
				Matcher matcher = pattern.matcher(strClsAuth);
				String p = null;
				if (matcher.find()) {
					p = matcher.group(1);
					logger.debug("\tPermission: " + p);
					List<Permission> tmp = permissionRepository.getAll("p.name", p);
					Permission parentPermission = null;
					if (tmp.isEmpty()) {
						parentPermission = new Permission(p, p);
						Status s = permissionRepository.create(parentPermission);
						if (!s.isSuccess()) {
							throw new Exception(s.getMessage());
						}
						inserts.add(s.getGeneratedId());
						parentPermission = permissionRepository.findById(s.getGeneratedId());
					} else {
						parentPermission = tmp.get(0);
						parentPermission.setName(p);
						permissionRepository.update(parentPermission);
					}

					Reflections refM = new Reflections(
							"tz.co.nezatech.apps.survey.web.controller." + cl.getSimpleName(),
							new MethodAnnotationsScanner());

					logger.debug("\tMethods: ");
					for (Method md : refM.getMethodsAnnotatedWith(PreAuthorize.class)) {

						PreAuthorize preAuth = md.getAnnotation(PreAuthorize.class);
						String strAuth = preAuth.value();
						Matcher mat = pattern.matcher(strAuth);
						String mp = null;
						if (mat.find()) {
							mp = mat.group(1);
							logger.debug("\t\t" + md.getName() + ": " + mp);
							tmp = permissionRepository.getAll("p.name", mp);
							Permission permission = null;
							if (tmp.isEmpty()) {
								permission = new Permission(mp, mp);
								permission.setParent(parentPermission);
								Status s = permissionRepository.create(permission);
								if (!s.isSuccess()) {
									throw new Exception(s.getMessage());
								}
								inserts.add(s.getGeneratedId());
							}else {
								permission=tmp.get(0);
								permission.setName(mp);
								permissionRepository.update(permission);
							}
						}
					}
				}

			}

			if (!inserts.isEmpty()) {
				List<Role> tmp = roleRepository.getAll("r.name", ROOT_ROLE_NAME);
				if (!tmp.isEmpty()) {
					Role e = tmp.get(0);
					roleRepository.matrixInserts(inserts, e);
					logger.debug("Successfully added permissions for root: " + ROOT_ROLE_NAME);
				} else {
					logger.error("There is no role: " + ROOT_ROLE_NAME);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent arg0) {
		initPermissions();
	}
}

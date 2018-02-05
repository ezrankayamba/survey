package tz.co.nezatech.apps.survey.model;

import tz.co.nezatech.apps.util.nezadb.model.BaseData;

public class NamedData extends BaseData {
	private Long id;
	private String name, description;

	public NamedData() {
	}

	public NamedData(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		if (id != null && id <= 0)
			this.id = null;
		else
			this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof NamedData))
			return false;

		return this.getId().equals(((NamedData) obj).getId());
	}

}

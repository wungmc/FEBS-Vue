/*
 * Copyright (C), 2011-2019.
 */
package cc.mrbird.febs.system.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author wung 2019/3/26.
 */
@Data
public class Book implements Serializable {
	
	private static final long serialVersionUID = 8444900939631263983L;
	
	public Book() {}
	
	public Book(Integer id, String name, String author, Date pulishDate) {
		this.id = id;
		this.name = name;
		this.author = author;
		this.pulishDate = pulishDate;
	}
	
	private Integer id;
	
	private String name;
	
	private String author;
	
	private Date pulishDate;
	
}

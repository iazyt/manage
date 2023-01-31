package com.manage.back_jdk8.entity;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BaseEntity implements Serializable {


	private Long id;

	private LocalDateTime created;
	private LocalDateTime updated;

	private Integer statu;
}

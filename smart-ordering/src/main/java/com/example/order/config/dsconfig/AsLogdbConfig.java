package com.example.order.config.dsconfig;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@MapperScan(basePackages = {"com.example.order.dao"}, sqlSessionFactoryRef = "aslogdbSqlSessionFactory")
public class AsLogdbConfig {

  @Bean(name = "aslogdbDataSource")
  @ConfigurationProperties("spring.datasource.aslogdb")
  public DataSource csdbv2DataSource() {
    return DruidDataSourceBuilder.create().build();
  }


  @Bean(name = "aslogdbSqlSessionFactory")
  public SqlSessionFactory csdbv2SqlSessionFactory(@Qualifier("aslogdbDataSource") DataSource diaryDataSource) throws Exception {
    final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    sessionFactory.setDataSource(diaryDataSource);
    Objects.requireNonNull(sessionFactory.getObject()).getConfiguration().setMapUnderscoreToCamelCase(true);
    return sessionFactory.getObject();
  }
}

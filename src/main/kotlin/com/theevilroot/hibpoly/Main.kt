package com.theevilroot.hibpoly

import com.theevilroot.hibpoly.model.DiscriminatorAnalyticsEvent
import com.theevilroot.hibpoly.model.DiscriminatorConnectEvent
import com.theevilroot.hibpoly.model.DiscriminatorCrashEvent
import com.theevilroot.hibpoly.model.DiscriminatorDataEvent
import com.theevilroot.hibpoly.model.DiscriminatorDisconnectEvent
import com.theevilroot.hibpoly.model.DiscriminatorEvent
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import org.hibernate.cfg.Environment
import java.util.*

fun main() {
    val configurationProperties = Properties().apply {
        put(Environment.CONNECTION_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider")

        put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect")
        put("hibernate.hikari.dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")

        put("hibernate.hikari.maximumPoolSize", "64")
        put("hibernate.show_sql", "false")
        put("hibernate.format_sql", "false")
        put("hibernate.use_sql_comments", "false")
        put("hibernate.hikari.idleTimeout", "30000")
        put("hibernate.hikari.dataSource.url", "jdbc:postgresql://10.80.0.4:5432/postgres")
        put("hibernate.hikari.dataSource.user", "postgres")
        put("hibernate.hikari.dataSource.password", "postgres")
        put("hibernate.hbm2ddl.auto", "update")
        put("hibernate.connection.autocommit", "false")
    }

    val configuration = Configuration().apply {
        properties = configurationProperties
        addAnnotatedClass(DiscriminatorEvent::class.java)
        addAnnotatedClass(DiscriminatorConnectEvent::class.java)
        addAnnotatedClass(DiscriminatorDisconnectEvent::class.java)
        addAnnotatedClass(DiscriminatorDataEvent::class.java)
        addAnnotatedClass(DiscriminatorAnalyticsEvent::class.java)
        addAnnotatedClass(DiscriminatorCrashEvent::class.java)
    }

    val serviceRegistry = StandardServiceRegistryBuilder()
        .applySettings(configuration.properties)
        .build()

    val sessionFactory = configuration
        .buildSessionFactory(serviceRegistry)

    val server = HttpServer(sessionFactory)
    server.start()
}


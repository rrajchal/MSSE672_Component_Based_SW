module mysql.connector.j {
    requires java.management;

    requires transitive java.logging;
    requires transitive java.naming;
    requires transitive java.security.sasl;
    requires transitive java.sql;
    requires transitive java.transaction.xa;
    requires transitive java.xml;

    exports com.mysql.cj;
    exports com.mysql.cj.admin;
    exports com.mysql.cj.callback;
    exports com.mysql.cj.conf;
    exports com.mysql.cj.conf.url;
    exports com.mysql.cj.exceptions;
    exports com.mysql.cj.interceptors;
    exports com.mysql.cj.jdbc;
    exports com.mysql.cj.jdbc.admin;
    exports com.mysql.cj.jdbc.exceptions;
    exports com.mysql.cj.jdbc.ha;
    exports com.mysql.cj.jdbc.integration.c3p0;
    exports com.mysql.cj.jdbc.interceptors;
    exports com.mysql.cj.jdbc.jmx;
    exports com.mysql.cj.jdbc.result;
    exports com.mysql.cj.jdbc.util;
    exports com.mysql.cj.log;
    exports com.mysql.cj.protocol;
    exports com.mysql.cj.protocol.a;
    exports com.mysql.cj.protocol.a.authentication;
    exports com.mysql.cj.protocol.a.result;
    exports com.mysql.cj.protocol.result;
    exports com.mysql.cj.protocol.x;
    exports com.mysql.cj.result;
    exports com.mysql.cj.sasl;
    exports com.mysql.cj.util;
    exports com.mysql.cj.x.protobuf;
    exports com.mysql.cj.xdevapi;
    exports com.mysql.jdbc;

    provides java.sql.Driver with
        com.mysql.cj.jdbc.Driver;

}

module server {
    requires java.xml.ws;
     requires jdk.httpserver;
      requires java.logging; 

    // generated by WebServiceWrapperGenerator
    exports server.http_multi_cookie_portable.server.jaxws;
    exports server.http_multi_cookie_portable.server;
}

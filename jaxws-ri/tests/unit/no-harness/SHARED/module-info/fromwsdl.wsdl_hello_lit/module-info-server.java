module server {
    requires java.xml.ws;
      requires jdk.httpserver;
      requires java.logging; 

    // generated by WsimportTool
    exports fromwsdl.wsdl_hello_lit.server;
    exports fromwsdl.wsdl_hello_lit.common;
}

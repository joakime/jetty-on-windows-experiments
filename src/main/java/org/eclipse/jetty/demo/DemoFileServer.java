package org.eclipse.jetty.demo;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.PathResource;

public class DemoFileServer
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);

        // Find base directory for this context
        Path basePath = Paths.get("webapp-base").toAbsolutePath();
        if (!Files.exists(basePath))
            throw new FileNotFoundException("WebApp Base: " + basePath);

        // Create a app (with context) for this server
        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        handler.setBaseResource(new PathResource(basePath));
        ServletHolder defHolder = new ServletHolder("default", DefaultServlet.class);
        // disable next line to disable file listings
        defHolder.setInitParameter("dirAllowed", "true");
        handler.addServlet(defHolder, "/");

        // Add app to handler tree
        HandlerList handlers = new HandlerList();
        handlers.addHandler(handler);
        handlers.addHandler(new DefaultHandler());

        // Add handler tree to server
        server.setHandler(handlers);

        // start server
        server.start();
        server.join();
    }
}

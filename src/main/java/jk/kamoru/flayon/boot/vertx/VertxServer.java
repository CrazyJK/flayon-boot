package jk.kamoru.flayon.boot.vertx;

import javax.annotation.PostConstruct;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.ServerWebSocket;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxServer {

	@PostConstruct
	public void start() {
		log.info("Vert.x server starting...");
		Vertx vertx = Vertx.newVertx();
		vertx.createHttpServer().websocketHandler(new Handler<ServerWebSocket>() {

			@Override
			public void handle(ServerWebSocket ws) {
				if (ws.path.equals("/myapp")) {
					ws.dataHandler(new Handler<Buffer>() {

						@Override
						public void handle(Buffer data) {
							ws.writeTextFrame(data.toString()); // Echo it back
							
						}
					});
				}
				else {
					ws.reject();
				}
			}
			
		}).requestHandler(new Handler<HttpServerRequest>() {

			@Override
			public void handle(HttpServerRequest req) {
				if (req.path.equals("/"))
					req.response.sendFile("websockets/ws.html"); // Serve the html
			}
			
		}).listen(8081);
		log.info("Vert.x server started!");
	}
}

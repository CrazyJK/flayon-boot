package jk.crazy.flayon.scheduling;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * async method<br>
 * <code>@</code>EnableAsync 설정 필요
 * <pre>
 *   <code>@</code>SpringBootApplication
 *   <code>@</code>EnableAsync
 *   public class FlayOnApplication {
 *   ...
 * </pre>
 * @author kamoru
 *
 */
@Component
@Slf4j
public class AsyncWatchDirectorySample {

	@Async
	public Future<Object> start() throws IOException {
		WatchService watcher = FileSystems.getDefault().newWatchService();
		Path path = Paths.get(".");
		log.info("Start watch service : {}", path.toAbsolutePath());
		path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		
		while (true) {
			WatchKey key = null;
			try {
				key = watcher.take();
			} catch (InterruptedException e) {
				return new AsyncResult<Object>(e);
			}
			
			for (WatchEvent<?> _event : key.pollEvents()) {
				Kind<?> kind = _event.kind();
				@SuppressWarnings("unchecked")
				WatchEvent<Path> event = (WatchEvent<Path>) _event;
				Path filename = event.context();
				log.info("{} : {}", kind.name(), filename);
				
			}
			if (!key.reset()) {
				break;
			}
		}
		return new AsyncResult<Object>(path);
	}

}

package jk.crazy.flayon.watch;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;

import static java.nio.file.StandardWatchEventKinds.*;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WatchServiceSample {
	
	public void start() {
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.submit(new Callable<Path>() {

			@Override
			public Path call() throws Exception {
				WatchService watcher = FileSystems.getDefault().newWatchService();
				Path path = Paths.get("E:\\Enter");
				path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
				
				while (true) {
					WatchKey key = null;
					try {
						key = watcher.take();
					} catch (InterruptedException e) {
						return null;
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
				return null;
			}});
	}

}

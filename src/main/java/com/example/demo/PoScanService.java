package com.example.demo;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import common.po.PoScanFile;
import common.utils.MyFiles;
import common.utils.MyUtils;

@Service
public class PoScanService {

	@Async
	public void run(SpringConfig config, String targetPath) {
		PoScanFile scan;
		
		scan = new PoScanFile(config, targetPath);
		scan.scanRemainedFile();	//すでにフォルダにあるpdfをScan
		//指定ディレクトリ配下のファイルのみ(またはディレクトリ)を取得
		// https://qiita.com/fumikomatsu/items/67f012b364dda4b03bf1
		try {
	        WatchService watcher;
			watcher = FileSystems.getDefault().newWatchService();
			Path dir = Paths.get(targetPath);
			dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			MyUtils.SystemLogPrint("■PoScanService: start..." + targetPath);
	        for (;;) {
	            WatchKey watchKey = watcher.take();
	            for (WatchEvent<?> event: watchKey.pollEvents()) {
	                if (event.kind() == OVERFLOW) continue;
	                //新規作成
	                if (event.kind() == ENTRY_CREATE) {
	                    WatchEvent<Path> ev = cast(event);
	                    Path name = ev.context();		//ファイル名
	                    Path src = dir.resolve(name);	//フルパス
	                    String fileName = name.toString();
	                    System.out.format("%s: %s %s\n", event.kind().name(), src, name);
						if (MyFiles.isFile(src.toString())) {
                    		MyUtils.SystemLogPrint("  ファイル検出: " + fileName);
                    		scan.scanProcess(src.toString());
						}
	                }
	            }
	            watchKey.reset();
	        }
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
}

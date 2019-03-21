# Magic Copy

This project focus on java file copy. support multi thread copy.

## Usage scenario
In many case, we want to copy a file or folder to other place, like offline data update.
If you want get the copy progress in UI to tell user what our program doing. you can use this library to solved this easy


## How to use
Use this library, if you use gradle,  you need a repo to your project.
```
repositories {
	maven {
		url  "https://dl.bintray.com/quenlenliu/Magic"
	}
}
```

and then add the dependence.

`compile 'org.quenlen.magic:magic-copy:1.0`

Fow now your can use this library.

```
        File src = new File("your src file");
        File dest = new File("your dest file");
        final int desireThread = 4; // Your desire running thread. we limit it to 0 < desireThread <= availableProcessors
        CopyService.createTask(src, dest, desireThread, new IListener() {
            @Override
            public void onStart() {
                // Task start running
            }

            @Override
            public void onComplete() {
                // Task running complete success.
            }

            @Override
            public void onError() {
                // Something error, if find error, will stop all child task.
            }

            @Override
            public void onCancel() {
                // Cancel, if your call task.cancel()
            }

            @Override
            public void onProgress(long cur, long total) {
                // the progress.
            }
        }).execute();
```

If you want use the jar direct. you can download from  this website
> https://bintray.com/beta/#/quenlenliu/Magic/magic-copy?tab=files


## Issues
Now was developing, so many function not open to user. if you want more function, please report and issue in github, I
will support in next version.


## Next step support

Now this library's thread unit is one file one thread. I want to support one file multi thread

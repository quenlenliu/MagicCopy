package org.nio.copy.usb;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

final class FileCopyTask extends Task {
    private File mSource;
    private File mTarget;
    private File mTemp;
    public FileCopyTask(ParentTask parent, File source, File dest) {
        this(parent, source, dest, false);
    }

    /**
     * Define a task.
     * @param source
     * @param dest
     * @param protect if is true, define a temp file to execute this task.
     */
    public FileCopyTask(ParentTask parent, File source, File dest, boolean protect) {
        super(parent);
        init(source, dest, protect);
    }

    @Override
    public String getName() {
        return mSource.getName();
    }

    private void init(File source, File dest, boolean protect) {
        if (!source.isFile()) {
            throw new RuntimeException("Source is not file: " + source.getAbsolutePath());
        }

        mSource = source;
        mTarget = dest;
        if (protect) {
            mTemp = new File(mTarget.getAbsolutePath() + ".tmp");
        }
    }

    private boolean createNewFile(File file) throws IOException {
        boolean success = true;
        File parent = file.getParentFile();
        if (!parent.exists()) {
            success = parent.mkdirs();
        }
        if (success) {
            success = file.createNewFile();
        }
        return success;
    }

    @Override
    protected int onExecute() {
        int resultFlag;
        try {
            notifyNewProgress(0);
            Path fromPath = Paths.get(mSource.toURI());
            Path toPath;
            if (mTemp != null) {
                createNewFile(mTemp);
                toPath = Paths.get(mTemp.toURI());
            } else {
                createNewFile(mTarget);
                toPath = Paths.get(mTarget.toURI());
            }

            Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
            if (mTemp != null) {
                mTemp.renameTo(mTarget);
            }
            notifyNewProgress(getTaskLoad());
            resultFlag = FLAG_STATE_COMPLETE;
        } catch (Exception e) {
            e.printStackTrace();
            resultFlag = FLAG_STATE_ERROR;
        } finally {

        }
        return resultFlag;
    }

    @Override
    public long getTaskLoad() {
        return mSource.length();
    }
}

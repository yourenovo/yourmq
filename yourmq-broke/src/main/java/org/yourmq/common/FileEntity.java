package org.yourmq.common;

import org.yourmq.utils.UnmapUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class FileEntity extends EntityDefault {
    private final RandomAccessFile fileRaf;
    private final FileChannel fileC;

    public FileEntity(File file) throws IOException {
        long len = file.length();
        this.fileRaf = new RandomAccessFile(file, "r");
        this.fileC = this.fileRaf.getChannel();
        MappedByteBuffer byteBuffer = this.fileC.map(FileChannel.MapMode.READ_ONLY, 0L, len);
        this.dataSet(byteBuffer);
        this.metaPut("Data-Disposition-Filename", file.getName());
    }

    @Override
    public void release() throws IOException {
        if (this.data() instanceof MappedByteBuffer) {
            UnmapUtil.unmap(this.fileC, (MappedByteBuffer)this.data());
        }

        this.fileRaf.close();
    }
}
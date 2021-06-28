package com.inject.plugin.converter;

/**
 * Created time : 2021/6/28 6:54.
 *
 * @author 10585
 */
public class DeleteLine {
    public final int start;
    public final int end;

    public DeleteLine(int start, int end) {
        this.start = start;
        this.end = end;
    }
}
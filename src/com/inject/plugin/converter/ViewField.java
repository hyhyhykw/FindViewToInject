package com.inject.plugin.converter;

/**
 * Created time : 2021/6/27 15:40.
 *
 * @author 10585
 */
public class ViewField {
    public final String name;
    public final String type;
    public final String id;

    public ViewField(String name, String type, String id) {
        this.name = name;
        this.type = type;
        this.id = id;
    }
}
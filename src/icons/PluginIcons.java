package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created time : 2021/6/25 9:29.
 *
 * @author 10585
 */
public interface PluginIcons {
    Icon ICON = IconLoader.getIcon("/icons/icon-converter.png", PluginIcons.class);

//    Icon ICON_16= IconManager.getInstance().loadRasterizedIcon("/icons/inject-helper.svg", PluginIcons.class.getClassLoader(), 8630828675481524949L, 2);
}

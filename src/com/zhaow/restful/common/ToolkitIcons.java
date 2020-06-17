//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zhaow.restful.common;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.icons.AllIcons.Modules;
import com.intellij.openapi.util.IconLoader;
import com.zhaow.restful.method.HttpMethod;
import javax.swing.Icon;

public class ToolkitIcons {
    public static final Icon MODULE;
    public static final Icon Refresh;
    public static final Icon SERVICE;

    public ToolkitIcons() {
    }

    static {
        MODULE = Modules.SourceRoot;
        Refresh = Actions.Refresh;
        SERVICE = IconLoader.getIcon("/icons/service.png");
    }

    public static class METHOD {
        public static Icon GET = IconLoader.getIcon("/icons/method/g.png");
        public static Icon PUT = IconLoader.getIcon("/icons/method/p2.png");
        public static Icon POST = IconLoader.getIcon("/icons/method/p.png");
        public static Icon PATCH = IconLoader.getIcon("/icons/method/p3.png");
        public static Icon DELETE = IconLoader.getIcon("/icons/method/d.png");
        public static Icon UNDEFINED = IconLoader.getIcon("/icons/method/undefined.png");

        public METHOD() {
        }

        public static Icon get(HttpMethod method) {
            if (method == null) {
                return UNDEFINED;
            } else if (method.equals(HttpMethod.GET)) {
                return GET;
            } else if (method.equals(HttpMethod.POST)) {
                return POST;
            } else if (!method.equals(HttpMethod.PUT) && !method.equals(HttpMethod.PATCH)) {
                return method.equals(HttpMethod.DELETE) ? DELETE : null;
            } else {
                return PUT;
            }
        }
    }
}

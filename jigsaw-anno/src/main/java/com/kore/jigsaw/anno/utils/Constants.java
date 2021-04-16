package com.kore.jigsaw.anno.utils;

/**
 * @author koreq
 * @date 2021-04-13
 * @description 常量
 */
public interface Constants {
    String KEY_MODULE_NAME = "module";

    String ANNO_PKG = "com.kore.jigsaw.anno.router";
    String ANNOTATION_TYPE_ROUTE = ANNO_PKG + ".Route";
    String ANNOTATION_TYPE_AUTOWIRED = ANNO_PKG + ".Autowired";

    String JIGSAW_CORE_PKG = "com.kore.jigsaw.core";

    String SEPARATOR = "$$";
    String PROJECT = "JRouter";
    String SUFFIX_AUTOWIRED = SEPARATOR + PROJECT + SEPARATOR + "Autowired";    // $$JRouter$$Autowired

    String INTERFACE_AUTOWIRED = "com.kore.jigsaw.core.router.BaseAutowired";
    String BASE_MODULE_ROUTER = "com.kore.jigsaw.core.router.BaseModuleRouter";
    String SUFFIX_ROUTER_MAP = "JRouterMap";

    String PREFIX_OF_LOGGER = "[Kore-JRouter-Apt] ";

    // System interface
    String ACTIVITY = "android.app.Activity";
    String FRAGMENT = "android.app.Fragment";
    String FRAGMENT_V4 = "android.support.v4.app.Fragment";
    String SERVICE = "android.app.Service";
    String PARCELABLE = "android.os.Parcelable";

    // Java type
    String LANG = "java.lang";
    String BYTE = LANG + ".Byte";
    String SHORT = LANG + ".Short";
    String INTEGER = LANG + ".Integer";
    String LONG = LANG + ".Long";
    String FLOAT = LANG + ".Float";
    String DOUBEL = LANG + ".Double";
    String BOOLEAN = LANG + ".Boolean";
    String CHAR = LANG + ".Character";
    String STRING = LANG + ".String";
    String SERIALIZABLE = "java.io.Serializable";
}

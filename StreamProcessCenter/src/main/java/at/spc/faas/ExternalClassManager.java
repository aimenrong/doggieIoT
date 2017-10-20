package at.spc.faas;

import at.spc.exception.ExternalClassManagerException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by eyonlig on 9/22/2017.
 */
@Component("ExternalClassManager")
public class ExternalClassManager {
    private Map<String, URLClassLoader> loaderStore = new ConcurrentHashMap<>();
    private URLClassLoader loader;

    public void loadExternalJar(String functionChainId, String jarRootPath) {
        File jarDirectory = new File(jarRootPath);
        URL[] classpath = new URL[]{};
        if (jarDirectory.exists() && jarDirectory.isDirectory()) {
            @SuppressWarnings("unchecked")
            File[] jars = jarDirectory.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".jar")) {
                        return true;
                    }
                    return false;
                }
            });
            List<URL> urls = new ArrayList<URL>(jars.length);
            for (File jar : jars) {
                try {
                    urls.add(jar.toURI().toURL());
                    for (URL url : urls) {
                        System.out.println(url.toString());
                    }
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
            }
            classpath = urls.toArray(new URL[urls.size()]);
        }
        loader = new URLClassLoader(classpath, null);
        loaderStore.put(functionChainId, loader);
    }

    public Object loadInstance(String functionChainId, String className) {
        try {
            @SuppressWarnings("unchecked")
            Class dynamicPart =
                    Class.forName(className,
                            true, loaderStore.get(functionChainId));
            return dynamicPart.newInstance();
        } catch (Exception e) {
            throw new ExternalClassManagerException(e);
        }
    }

    public Class loadClass(String functionChainId, String className) {
        try {
            @SuppressWarnings("unchecked")
            Class dynamicPart =
                    Class.forName(className,
                            true, loaderStore.get(functionChainId));
            return dynamicPart;
        } catch (Exception e) {
            throw new ExternalClassManagerException(e);
        }
    }
}

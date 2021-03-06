package com.github.catvod.crawler;

import android.content.Context;

import com.github.tvbox.osc.base.App;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dalvik.system.DexClassLoader;

public class JarLoader {
    private DexClassLoader classLoader = null;
    private ConcurrentHashMap<String, Spider> spiders = new ConcurrentHashMap<>();
    private Method proxyFun = null;

    /**
     * 不要在主线程调用我
     *
     * @param jarData
     */
    public boolean load(byte[] jarData) {
        spiders.clear();
        proxyFun = null;
        boolean success = false;
        try {
            File cacheDir = new File(App.getInstance().getCacheDir().getAbsolutePath() + "/catvod_csp");
            if (!cacheDir.exists())
                cacheDir.mkdirs();
            String cache = App.getInstance().getCacheDir().getAbsolutePath() + "/catvod_csp.jar";
            FileOutputStream fos = new FileOutputStream(cache);
            fos.write(jarData);
            fos.flush();
            fos.close();
            classLoader = new DexClassLoader(cache, cacheDir.getAbsolutePath(), null, App.getInstance().getClassLoader());
            // make force wait here, some device async dex load
            int count = 0;
            do {
                try {
                    Class classInit = classLoader.loadClass("com.github.catvod.spider.Init");
                    if (classInit != null) {
                        success = true;
                        Method method = classInit.getMethod("init", Context.class);
                        method.invoke(null, App.getInstance());
                        System.out.println("自定义爬虫代码加载成功!");
                        try {
                            Class proxy = classLoader.loadClass("com.github.catvod.spider.Proxy");
                            Method mth = proxy.getMethod("proxy", Map.class);
                            proxyFun = mth;
                        } catch (Throwable th) {

                        }
                        break;
                    }


                    Thread.sleep(200);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
                count++;
            } while (count < 5);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return success;
    }

    public Spider getSpider(String key, String ext) {
        String clsKey = key.replace("csp_", "");
        if (spiders.contains(clsKey))
            return spiders.get(clsKey);
        if (classLoader == null)
            return new SpiderNull();
        try {
            Spider sp = (Spider) classLoader.loadClass("com.github.catvod.spider." + clsKey).newInstance();
            sp.init(App.getInstance(), ext);
            spiders.put(clsKey, sp);
            return sp;
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return new SpiderNull();
    }

    public Object[] proxyInvoke(Map params) {
        try {
            if (proxyFun != null) {
                return (Object[]) proxyFun.invoke(null, params);
            }
        } catch (Throwable th) {

        }
        return null;
    }
}

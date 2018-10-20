package com.github.cloudyrock.proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @since 04/04/2018
 */
class ProxyMethodInterceptor implements MethodInterceptor {

  private final Object original;
  private final ProxyFactory proxyFactory;
  private final Set<String> proxyCreatorMethods;
  private final Set<String> freeFromInterceptionMethods;
  private final PreInterceptor preInterceptor;

  ProxyMethodInterceptor(Object original,
                         ProxyFactory proxyFactory,
                         PreInterceptor preInterceptor,
                         Set<String> proxyCreatorMethods,
                         Set<String> freeFromInterceptionMethods) {
    this.original = original;
    this.preInterceptor = preInterceptor;
    this.proxyFactory = proxyFactory;
    this.proxyCreatorMethods = proxyCreatorMethods != null ? proxyCreatorMethods : Collections.<String>emptySet();
    this.freeFromInterceptionMethods = freeFromInterceptionMethods != null ? freeFromInterceptionMethods : Collections.<String>emptySet();
  }

  @Override
  public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
    checkMethod(method);
    return invokeMethod(method, objects);

  }

  private void checkMethod(Method method) {
    if (!freeFromInterceptionMethods.contains(method.getName())) {
      preInterceptor.before();
    }
  }

  private Object invokeMethod(Method method, Object[] objects) throws IllegalAccessException, InvocationTargetException {
    if (proxyCreatorMethods.contains(method.getName())) {
      return proxyFactory.createProxyFromOriginal(method.invoke(original, objects));
    } else {
      return method.invoke(original, objects);
    }
  }
}

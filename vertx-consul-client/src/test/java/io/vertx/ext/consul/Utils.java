/*
 * Copyright (c) 2016 The original author or authors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *      The Eclipse Public License is available at
 *      http://www.eclipse.org/legal/epl-v10.html
 *
 *      The Apache License v2.0 is available at
 *      http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package io.vertx.ext.consul;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:ruslan.sennov@gmail.com">Ruslan Sennov</a>
 */
public class Utils {

  public static String readResource(String fileName) throws Exception {
    final InputStream is = Utils.class.getClassLoader().getResourceAsStream(fileName);
    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(is))) {
      return buffer.lines().collect(Collectors.joining("\n"));
    }
  }

  public static void runAsync(Consumer<Handler<AsyncResult<Void>>> runner) {
    CountDownLatch latch = new CountDownLatch(1);
    Future<Void> future = Future.future();
    runner.accept(h -> {
      if (h.succeeded()) {
        future.complete();
      } else {
        future.fail(h.cause());
      }
      latch.countDown();
    });
    try {
      latch.await(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (future.failed()) {
      throw new RuntimeException(future.cause());
    }
  }

  public static <T> T getAsync(Consumer<Handler<AsyncResult<T>>> runner) {
    CountDownLatch latch = new CountDownLatch(1);
    Future<T> future = Future.future();
    runner.accept(h -> {
      if (h.succeeded()) {
        future.complete(h.result());
      } else {
        future.fail(h.cause());
      }
      latch.countDown();
    });
    try {
      latch.await(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (future.succeeded()) {
      return future.result();
    } else {
      throw new RuntimeException(future.cause());
    }
  }

  public static void sleep(Vertx vertx, long millis) {
    CountDownLatch latch = new CountDownLatch(1);
    vertx.setTimer(millis, h -> latch.countDown());
    try {
      latch.await(2 * millis, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static String osResolve() {
    String os = System.getProperty("os.name").toLowerCase();
    String binaryVersion = "linux";
    if (os.contains("mac")) {
      binaryVersion = "darwin";
    } else if (os.contains("windows")) {
      binaryVersion = "windows";
    }
    return binaryVersion;
  }

  public static boolean isWindows() {
    return "windows".equals(osResolve());
  }

  public static int getFreePort() {
    int port = -1;
    try {
      ServerSocket socket = new ServerSocket(0);
      port = socket.getLocalPort();
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return port;
  }
}
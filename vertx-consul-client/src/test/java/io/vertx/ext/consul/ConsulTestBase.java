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

import io.vertx.core.Vertx;
import io.vertx.test.core.VertxTestBase;

import java.util.function.Function;

/**
 * @author <a href="mailto:ruslan.sennov@gmail.com">Ruslan Sennov</a>
 */
public class ConsulTestBase extends VertxTestBase {

  protected static Function<Vertx, ConsulContext> ctxFactory;
  protected ConsulContext ctx;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    ctx = ctxFactory.apply(vertx);
    ctx.start();
  }

  @Override
  public void tearDown() throws Exception {
    ctx.stop();
    ctx = null;
    super.tearDown();
  }

}

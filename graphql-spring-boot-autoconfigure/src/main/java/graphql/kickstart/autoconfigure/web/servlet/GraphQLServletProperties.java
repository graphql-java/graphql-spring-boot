/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 oEmbedler Inc. and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 *  persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package graphql.kickstart.autoconfigure.web.servlet;

import graphql.kickstart.execution.context.ContextSetting;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

/** @author Michiel Oliemans */
@Data
@ConfigurationProperties(prefix = "graphql.servlet")
public class GraphQLServletProperties {

  public final static Duration DEFAULT_SUBSCRIPTION_TIMEOUT = Duration.ZERO;

  private boolean enabled = true;
  private boolean corsEnabled = true;
  private String mapping = "/graphql";
  private boolean exceptionHandlersEnabled = false;
  /**
   * Subscription timeout. If a duration suffix is not specified, millisecond will be used.
   */
  @DurationUnit(ChronoUnit.MILLIS)
  private Duration subscriptionTimeout = DEFAULT_SUBSCRIPTION_TIMEOUT;
  private ContextSetting contextSetting = ContextSetting.PER_QUERY_WITH_INSTRUMENTATION;
  /** Asynchronous execution timeout. If a duration suffix is not specified, millisecond will be used. @deprecated Use <tt>graphql.servlet.async.timeout</tt> instead */
  @Deprecated @DurationUnit(ChronoUnit.MILLIS) private Duration asyncTimeout;
  /** @deprecated Use <tt>graphql.servlet.async.enabled</tt> instead */
  @Deprecated private Boolean asyncModeEnabled;

  private String tracingEnabled = "false";
  private boolean actuatorMetrics;
  private Integer maxQueryComplexity;
  private Integer maxQueryDepth;

  /**
   * @return the servlet mapping, coercing into an appropriate wildcard for servlets (ending in /*)
   */
  public String getServletMapping() {
    final String originalMapping = getMapping();
    if (mappingIsAntWildcard()) {
      return originalMapping.replaceAll("\\*$", "");
    } else if (mappingIsServletWildcard()) {
      return originalMapping;
    } else {
      return originalMapping.endsWith("/") ? originalMapping + "*" : originalMapping + "/*";
    }
  }

  /**
   * @return the servlet mapping, coercing into an appropriate wildcard for CORS, which uses ant
   *     matchers (ending in /**)
   */
  public String getCorsMapping() {
    final String originalMapping = getMapping();
    if (mappingIsAntWildcard()) {
      return originalMapping;
    } else if (mappingIsServletWildcard()) {
      return originalMapping + "*";
    } else {
      return originalMapping.endsWith("/") ? originalMapping + "**" : originalMapping + "/**";
    }
  }

  private boolean mappingIsServletWildcard() {
    return getMapping().endsWith("/*");
  }

  private boolean mappingIsAntWildcard() {
    return getMapping().endsWith("/**");
  }
}

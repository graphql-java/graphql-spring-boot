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
package graphql.kickstart.spring.web.boot;

import graphql.kickstart.execution.context.ContextSetting;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** @author <a href="mailto:java.lang.RuntimeException@gmail.com">oEmbedler Inc.</a> */
@Data
@ConfigurationProperties(prefix = "graphql.servlet")
public class GraphQLServletProperties {

  private boolean enabled = true;
  private boolean corsEnabled = true;
  private String mapping = "/graphql";
  private boolean exceptionHandlersEnabled = false;
  private long subscriptionTimeout = 0;
  private ContextSetting contextSetting = ContextSetting.PER_QUERY_WITH_INSTRUMENTATION;
  private long asyncTimeout = 30000;
  private boolean asyncModeEnabled = true;
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

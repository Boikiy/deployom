/*
 * The MIT License
 *
 * Copyright (c) 2014 DeployOM
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.deployom.data;

import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OracleLimit {

    private Integer current;
    private Integer initial;
    private Integer limit;
    private Integer max;
    private String resourceName;

    public OracleLimit() {
    }

    public Integer getCurrent() {
        return current;
    }

    public Integer getInitial() {
        return initial;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getMax() {
        return max;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public void setInitial(Integer initial) {
        this.initial = initial;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public String toString() {
        return this.resourceName;
    }
}

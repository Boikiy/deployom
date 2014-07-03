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
public class WeblogicSource {

    private Integer available;
    private Integer capacity;
    private Integer current;
    private Integer failures;
    private Integer high;
    private Integer leaked;
    private String sourceName;
    private Integer waiting;

    public WeblogicSource() {
    }

    public Integer getAvailable() {
        return available;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public Integer getCurrent() {
        return this.current;
    }

    public Integer getFailures() {
        return this.failures;
    }

    public Integer getHigh() {
        return this.high;
    }

    public Integer getLeaked() {
        return this.leaked;
    }

    public String getSourceName() {
        return this.sourceName;
    }

    public Integer getWaiting() {
        return waiting;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public void setFailures(Integer failures) {
        this.failures = failures;
    }

    public void setHigh(Integer high) {
        this.high = high;
    }

    public void setLeaked(Integer leaked) {
        this.leaked = leaked;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setWaiting(Integer waiting) {
        this.waiting = waiting;
    }

    @Override
    public String toString() {
        return this.sourceName;
    }
}

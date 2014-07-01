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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Chart {

    private String chartId;
    private String label1;
    private String label2;
    private String label3;
    private final List<Integer> line1;
    private final List<Integer> line2;
    private final List<Integer> line3;
    private List<String> ticks;
    private String title;

    public Chart() {
        ticks = new ArrayList<String>();
        line1 = new ArrayList<Integer>();
        line2 = new ArrayList<Integer>();
        line3 = new ArrayList<Integer>();
    }

    public Boolean addLine1(Integer line) {

        line1.add(line);
        return true;
    }

    public Boolean addLine2(Integer line) {

        line2.add(line);
        return true;
    }

    public Boolean addLine3(Integer line) {

        line3.add(line);
        return true;
    }

    public Boolean addTick(String tick) {

        ticks.add(tick);
        return true;
    }

    public String getChartId() {
        return this.chartId;
    }

    public String getLabel1() {
        return this.label1;
    }

    public String getLabel2() {
        return this.label2;
    }

    public String getLabel3() {
        return this.label3;
    }

    @XmlElement(name = "line1")
    public List<Integer> getLine1() {
        return line1;
    }

    @XmlElement(name = "line2")
    public List<Integer> getLine2() {
        return line2;
    }

    @XmlElement(name = "line3")
    public List<Integer> getLine3() {
        return line3;
    }

    @XmlElement(name = "tick")
    public List<String> getTicks() {
        return ticks;
    }

    public String getTitle() {
        return this.title;
    }

    public void setChartId(String chartId) {
        this.chartId = chartId;
    }

    public void setLabel1(String label1) {
        this.label1 = label1;
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }

    public void setLabel3(String label3) {
        this.label3 = label3;
    }

    public void setTicks(List<String> ticks) {
        this.ticks = ticks;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return this.chartId;
    }

}

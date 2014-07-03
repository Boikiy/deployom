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
public class OracleTablespace {

    private String autoextensible;
    private Integer currentMb;
    private String fileName;
    private Integer maxMb;
    private List<OracleSegment> segments;
    private String status;
    private String tablespaceName;
    private Integer usedMb;
    private Integer usedPercent;

    public OracleTablespace() {
        segments = new ArrayList<OracleSegment>();
    }

    public Boolean addSegment(OracleSegment segment) {
        segments.add(segment);
        return true;
    }

    public String getAutoextensible() {
        return autoextensible;
    }

    public Integer getCurrentMb() {
        return currentMb;
    }

    public String getFileName() {
        return fileName;
    }

    public Integer getMaxMb() {
        return maxMb;
    }

    @XmlElement(name = "segment")
    public List<OracleSegment> getSegments() {
        return segments;
    }

    public String getStatus() {
        return status;
    }

    public String getTablespaceName() {
        return tablespaceName;
    }

    public Integer getUsedMb() {
        return usedMb;
    }

    public Integer getUsedPercent() {
        return usedPercent;
    }

    public void setAutoextensible(String autoextensible) {
        this.autoextensible = autoextensible;
    }

    public void setCurrentMb(Integer currentMb) {
        this.currentMb = currentMb;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setMaxMb(Integer maxMb) {
        this.maxMb = maxMb;
    }

    public void setSegments(List<OracleSegment> segments) {
        this.segments = segments;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTablespaceName(String tablespaceName) {
        this.tablespaceName = tablespaceName;
    }

    public void setUsedMb(Integer usedMb) {
        this.usedMb = usedMb;
    }

    public void setUsedPercent(Integer usedPercent) {
        this.usedPercent = usedPercent;
    }

    @Override
    public String toString() {
        return this.tablespaceName;
    }
}

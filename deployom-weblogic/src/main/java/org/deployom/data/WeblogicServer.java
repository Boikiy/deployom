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
public class WeblogicServer {

    private final List<WeblogicSource> dataSources;
    private Long heapCurrent;
    private Long heapFree;
    private Long heapMax;
    private Integer openSocket;
    private String serverName;
    private String state;
    private Integer threadHogging;
    private Integer threadIdle;
    private Integer threadQueue;
    private Integer threadTotal;
    private Double throughput;
    private Integer transactionActive;
    private Long transactionCommitted;
    private Long transactionRolledBack;
    private Long transactionTotal;
    private Long uptime;

    public WeblogicServer() {
        dataSources = new ArrayList<WeblogicSource>();
    }

    public Boolean addDataSource(WeblogicSource dataSource) {

        if (getDataSource(dataSource.getSourceName()) != null) {
            return false;
        }

        dataSources.add(dataSource);
        return true;
    }

    public WeblogicSource getDataSource(String sourceName) {
        for (WeblogicSource dataSource : dataSources) {
            if (dataSource.getSourceName().equals(sourceName)) {
                return dataSource;
            }
        }

        return null;
    }

    @XmlElement(name = "dataSource")
    public List<WeblogicSource> getDataSources() {
        return dataSources;
    }

    public Long getHeapCurrent() {
        return heapCurrent;
    }

    public Long getHeapFree() {
        return heapFree;
    }

    public Long getHeapMax() {
        return heapMax;
    }

    public Integer getOpenSocket() {
        return openSocket;
    }

    public String getServerName() {
        return this.serverName;
    }

    public String getState() {
        return state;
    }

    public Integer getThreadHogging() {
        return threadHogging;
    }

    public Integer getThreadIdle() {
        return threadIdle;
    }

    public Integer getThreadQueue() {
        return threadQueue;
    }

    public Integer getThreadTotal() {
        return threadTotal;
    }

    public Double getThroughput() {
        return throughput;
    }

    public Integer getTransactionActive() {
        return transactionActive;
    }

    public Long getTransactionCommitted() {
        return transactionCommitted;
    }

    public Long getTransactionRolledBack() {
        return transactionRolledBack;
    }

    public Long getTransactionTotal() {
        return transactionTotal;
    }

    public Long getUptime() {
        return uptime;
    }

    public Boolean removeDataSource(WeblogicSource dataSource) {
        dataSources.remove(dataSource);
        return true;
    }

    public void setHeapCurrent(Long heapCurrent) {
        this.heapCurrent = heapCurrent;
    }

    public void setHeapFree(Long heapFree) {
        this.heapFree = heapFree;
    }

    public void setHeapMax(Long heapMax) {
        this.heapMax = heapMax;
    }

    public void setOpenSocket(Integer openSocket) {
        this.openSocket = openSocket;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setThreadHogging(Integer threadHogging) {
        this.threadHogging = threadHogging;
    }

    public void setThreadIdle(Integer threadIdle) {
        this.threadIdle = threadIdle;
    }

    public void setThreadQueue(Integer threadQueue) {
        this.threadQueue = threadQueue;
    }

    public void setThreadTotal(Integer threadTotal) {
        this.threadTotal = threadTotal;
    }

    public void setThroughput(Double throughput) {
        this.throughput = throughput;
    }

    public void setTransactionActive(Integer transactionActive) {
        this.transactionActive = transactionActive;
    }

    public void setTransactionCommitted(Long transactionCommitted) {
        this.transactionCommitted = transactionCommitted;
    }

    public void setTransactionRolledBack(Long transactionRolledBack) {
        this.transactionRolledBack = transactionRolledBack;
    }

    public void setTransactionTotal(Long transactionTotal) {
        this.transactionTotal = transactionTotal;
    }

    public void setUptime(Long uptime) {
        this.uptime = uptime;
    }

    @Override
    public String toString() {
        return this.serverName;
    }
}

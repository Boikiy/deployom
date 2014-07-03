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
public class OracleSession {

    private Integer count;
    private Integer elapsedSeconds;
    private String machine;
    private String message;
    private String program;
    private Integer progressPercent;
    private Integer remainingSeconds;
    private Integer sid;
    private String status;
    private String user;

    public OracleSession() {
    }

    public Integer getCount() {
        return count;
    }

    public Integer getElapsedSeconds() {
        return elapsedSeconds;
    }

    public String getMachine() {
        return machine;
    }

    public String getMessage() {
        return message;
    }

    public String getProgram() {
        return program;
    }

    public Integer getProgressPercent() {
        return progressPercent;
    }

    public Integer getRemainingSeconds() {
        return remainingSeconds;
    }

    public Integer getSid() {
        return sid;
    }

    public String getStatus() {
        return status;
    }

    public String getUser() {
        return user;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setElapsedSeconds(Integer elapsedSeconds) {
        this.elapsedSeconds = elapsedSeconds;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public void setProgressPercent(Integer progressPercent) {
        this.progressPercent = progressPercent;
    }

    public void setRemainingSeconds(Integer remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return this.machine;
    }
}

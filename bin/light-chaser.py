#!/usr/bin/env python
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# !/usr/bin/python

import os
import sys

LIGHT_CHASER_HOME = os.getenv("LIGHT_CHASER_HOME")
LIGHT_CHASER_CONF = os.path.join(LIGHT_CHASER_HOME, "conf")


def check_java():
    check_java_cmd = "java -version"
    ret = os.system(check_java_cmd)
    if ret != 0:
        print("Failed to find java, please add java to PATH")
        sys.exit(-1)


def java_parameters(paras):
    ret = []
    for para in paras:
        temp = para.strip()
        if temp != "":
            ret.append(temp)
    return ret


def exec_light_chaser_class(kclass, args=[]):
    args_str = " ".join(args)
    command = "java -cp conf;light-chaser.jar " + kclass + " " + args_str
    print "Running: " + command
    STATUS = os.execvp("java", java_parameters(command.split(" ")))
    # print("STATUS", STATUS)


def local_daemon(*args):
    exec_light_chaser_class(
        "group.chaoliu.lightchaser.core.daemon.LocalDaemon",
        args=args
    )


def photon(*args):
    exec_light_chaser_class(
        "group.chaoliu.lightchaser.core.daemon.photon.Photon"
    )


def help():
    pass


def print_usage(command=None):
    if command != None:
        print("\n\tlight_chaser command:", command)
    else:
        print("use common, likes: python light-chaser.py test")


def run():
    if len(sys.argv) <= 1:
        print_usage()
        sys.exit(-1)
    COMMAND = sys.argv[1]
    ARGS = sys.argv[2:]
    try:
        (COMMANDS.get(COMMAND, "help"))(*ARGS)
    except Exception as e:
        print(e)
        print_usage(COMMAND)
        sys.exit(-1)


COMMANDS = {"local_daemon": local_daemon, "photon": photon}

if __name__ == '__main__':
    check_java()
    run()

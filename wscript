#! /usr/bin/env python
# encoding: utf-8
import os.path

def options(opt):
    pass

def configure(conf):
    conf.load('java')
    conf.env.MANIFEST = conf.path.parent.find_node('MANIFEST').abspath()
    conf.env.CLASSPATH = ['.', conf.path.parent.ant_glob('**/org.sat4j.core.jar')[0].abspath()]
    conf.env.SOURCES = ['kodkod/ast', 'kodkod/engine', 'kodkod/instance', 'kodkod/util/collections', 'kodkod/util/ints', 'kodkod/util/nodes']

def build(bld):
    bld(features  = 'javac jar',
        srcdir    = bld.env.SOURCES, 
        outdir    = 'kodkod',
        compat    = '1.7',
        classpath = bld.env.CLASSPATH,
        manifest  = '../MANIFEST',
        basedir   = 'kodkod',
        destfile  = 'kodkod.jar')
    
    bld.install_files(dest='.', files='kodkod.jar')
    


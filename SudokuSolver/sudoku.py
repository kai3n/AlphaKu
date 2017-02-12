# coding: utf-8

import os
import pickle
import json

import numpy as np
from core.model import convolutional
from core.sudokuExtractor import Extractor
from core import sudopy

def get_cells(image_path):  # yields 9 * 9 = 81 cells

    import tensorflow as tf
    x = tf.placeholder("float", [None, 784])
    sess = tf.Session()

    with tf.variable_scope("convolutional"):
        keep_prob = tf.placeholder("float")
        y2, variables = convolutional(x, keep_prob)
    saver = tf.train.Saver(variables)
    saver.restore(sess, "/home/ec2-user/workspace/AlphaKu/SudokuSolver/core/convolutional.ckpt")

    for row in Extractor(os.path.abspath(image_path)).cells:
        for cell in row:
            a = sess.run(y2, feed_dict={x: np.reshape(cell, (1, 784)), keep_prob: 1.0}).flatten().tolist()
            digit = np.argmax(a)
            yield str(digit) if a[digit] > 0.9925 else '.'

def solver(image_path):
        grid = ''.join(cell for cell in get_cells(image_path))
        print(grid)
        s = sudopy.solve(grid)
        return s



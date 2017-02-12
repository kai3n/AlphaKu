import os
import sys
sys.path.append(os.path.join(os.path.dirname(__file__), 'mnist'))
import model
from tensorflow.examples.tutorials.mnist import input_data
data = input_data.read_data_sets("../data/", one_hot=True)
import numpy as np
import tensorflow as tf

x = tf.placeholder("float", [None, 784])
sess = tf.Session()

with tf.variable_scope("convolutional"):
    keep_prob = tf.placeholder("float")
    y2, variables = model.convolutional(x, keep_prob)
saver = tf.train.Saver(variables)
saver.restore(sess, "convolutional.ckpt")

def convolutional(input):
    return sess.run(y2, feed_dict={x: input, keep_prob: 1.0}).flatten().tolist()

#print np.argmax(convolutional(data.test.images[100:101]))

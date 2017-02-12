# AlphaKu

AlphaKu is an android application that solves a Sudoku puzzle by using image processing and deep learning.


## Prerequisites

This open source is based on Python 2.7.

```
Python 2.7
OpenCV
Android Studio
An android phone (Marshmallow 6.0 or higher) or a virtual device
```

## Quick Start and Installation

These instructions will get you a copy of the project up and run on your 
local machine for development and testing purposes. See deployment for notes 
on how to deploy the project on a live system.

To get started, download this repository.
~~~ sh
$ git clone https://github.com/kai3n/AlphaKu.git
~~~

Compile and run SudokuSolver project to setup a server.
```python
from SudokuSolver import sudoku
sudoku.solver("your_image_file.jpg")  # return list of answer key
python run.py
```

Import Lab-Intent_FileProvider project in Android Studio.

### Running the application and Purpose

Connect the android phone to your PC/MAC. If not, run a virtual device.

Run the project.

Take a picture of Sudoku.
Ex.
![Sudoku](http://www.educationworld.com/a_lesson/sudoku/images/sudoku_002.gif =250x250)

```
Taking a picture checks if user gave a permission to access the camera. Then, compress 
the picture into a .JEPG and encode it to bytes using Base64.
```

Wait till the image gets sent to the server and processed.

```
Once the picture is taken, the image is getting uploaded to the server(http) and the 
program runs image processing to correctly copy the clues. Then, it runs the AI program 
to solve Sudoku. The time to finish this step varies, depending on the quality of the picture.
```

Once the answer is displayed, compare it with the original to check if the ones that were 
already given are correctly copied over and check the answer.

```
Once it solves Sudoku, it passes down the answer as an array of JSon which will be
converted as an array of String and printed on the screen using Grid View.
```

Go back to Main Menu and repeat it as many times as you want.

## Authors

* **James Pak** - *02/12/17* - [kai3n](https://github.com/kai3n)
* **Taekyoon Choi** - *02/12/17* - [Taekyoon](https://github.com/Taekyoon)
* **Seungyun Lee** - *02/12/17* - [dltmddbs64](https://github.com/dltmddbs64)


## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* https://www.youtube.com/watch?v=vq2nnJ4g6N0
* https://github.com/aymericdamien/TensorFlow-Examples
* https://github.com/sugyan/tensorflow-mnist
* https://github.com/amitshekhariitbhu/Fast-Android-Networking

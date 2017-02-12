from flask import Flask, redirect, url_for, request, jsonify
from SudokuSolver import sudoku
import base64

app = Flask(__name__)

@app.route('/')
@app.route('/index')
def index():
   return 'Test Server'

@app.route('/uploader',methods = ['POST', 'GET'])
def upload():
   if request.method == 'POST':
      img_data = request.values['image']
      filename = request.values['ImageName']
      byte_data = base64.b64decode(img_data)
      path = 'source/' + filename
      with open(path , 'wb') as f:
        f.write(byte_data)
      ans = sudoku.solver(path)
      return jsonify(results=ans)

@app.route('/test')
def imagetest():
      return '''<html>
                <body>
            <img src='/home/ec2-user/workspace/AlphaKu/source/JPEG_20170212_080327_1352113953.jpg'></img>
</body>
</html>'''
if __name__ == '__main__':
   app.run(host='0.0.0.0',port=5000, threaded=True, debug = True)

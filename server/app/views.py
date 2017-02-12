from flask import render_template, request, jsonify
from app import app

@app.route('/')
@app.route('/index')

def index():
    return "Hello, World"

@app.route('/upload')
def upload():
    return render_template('upload.html')

@app.route('/uploader', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        f = request.files['file']
        f.save(f.filename)
        return 'file uploaded successfully'
        
@app.route('/jsontest')
def testjson():
    list = [
        {'param': 'foo', 'val': 2},
        {'param': 'bar', 'val': 10}
    ]
    return jsonify(results=list)

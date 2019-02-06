const server = require('http').createServer();
const io = require('socket.io')(server);
const spawn = require("child_process").spawn;
var ecgStatus;

  
io.sockets.on('connection', client => {
    console.log("client connected !");

    client.on('foo', function (message) { 
        inbound = JSON.stringify(message);

        const pythonProcess = spawn('python',["./script.py", inbound]);
        pythonProcess.stdout.on('data', (data) => {
        ecgStatus = Buffer.from(data, 'hex').toString('utf8');   
        });
        function print_status(){
            //console.log(ecgStatus);
            client.broadcast.emit('event', {status: ecgStatus , ecgArray: message});
        }  
    setTimeout(print_status, 5000);
    }); 
});
server.listen(3000);

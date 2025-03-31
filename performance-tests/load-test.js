import grpc from 'k6/net/grpc';
import {check, sleep} from 'k6'

const client = new grpc.Client();
client.load(['../src/main/proto'], 'name.proto');

export const options = {
    stages:[
        { target: 500, duration: '30s' },
        { target: 1000, duration: '30s' },
        { target: 1000, duration: '2m' },
        { target: 500, duration: '30s' },
        { target: 0, duration: '30s' },
    ]
};

export default function () {
    client.connect('localhost:9090', {
        plaintext: true,
        reflect: true
    });

    const data = { requestId: "test", name: randomChars(5) };
    const response = client.invoke('NameService/GetNameStats', data);

    check(response, {
        'status is OK': (r) => r && r.status === grpc.StatusOK,
    });

    client.close();
}

function randomChars(len) {
    let chars = '';
    while (chars.length < len) {
        chars += Math.random().toString(36).substring(2);
    }
    return chars.substring(0, len);
}
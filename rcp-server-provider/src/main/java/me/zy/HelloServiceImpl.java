package me.zy;

@RpcService(value = IHelloService.class,version="v1.0")
public class HelloServiceImpl implements IHelloService {
    @Override
    public String sayHello(String content) {
        System.out.println("[v1.0]request in sayHello:" + content);
        return "Say Hello:" + content;
    }

    @Override
    public String saveUser(User user) {
        System.out.println("[v1.0]request in saveUser:" + user);
        return "SUCCESS";
    }
}

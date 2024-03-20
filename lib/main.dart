import 'dart:io';
import 'package:unitest/uni.dart';
import 'package:flutter/material.dart';
import 'package:unitest/provider.dart';
import 'package:provider/provider.dart';
import 'package:easy_folder_picker/FolderPicker.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatelessWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  Widget build(BuildContext context) {
    var uniAppService = UniAppService();
    return ChangeNotifierProvider(
      create: (context) => UniProvider(uniAppService: uniAppService),
      child: Consumer<UniProvider>(
        builder: (context, provider, child) => Scaffold(
            appBar: AppBar(
              backgroundColor: Theme.of(context).colorScheme.inversePrimary,
              title: Text(title),
            ),
            body: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  const Text(
                    'You have pushed the button this many times:',
                  ),
                  Text(
                    '_counter',
                    style: Theme.of(context).textTheme.headlineMedium,
                  ),
                ],
              ),
            ),
            floatingActionButton: FloatingActionButton(
                onPressed: () {
                  provider.init();
                  provider.uniAppService.openMP(appid: '__UNI__G7A80E5A');
                },
                tooltip: 'open app',
                child: const Icon(Icons.add))),
      ),
    );
  }
}

Future<Directory?> _pickDirectory(BuildContext context) async {
  Directory? newDirectory = await FolderPicker.pick(
    context: context,
    message: 'hellow world',
    rootDirectory: Directory(FolderPicker.rootPath),
  );

  return newDirectory;
}

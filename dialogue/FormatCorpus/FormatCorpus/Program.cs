﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace FormatCorpus
{
    class Program
    {
        static void Main(string[] args)
        {
            var lines = File.ReadAllLines("corpus.txt").ToList().Where(l => !string.IsNullOrEmpty(l)).Select(l => "<s> " + l.ToUpper() + " </s>");

            File.WriteAllLines("fc.txt", lines);
        }
    }
}
-define(DCACHE, shuwadiskcache).

-define(SHUWA_WORKERS, shuwa_workers).
-define(SHUWA_WORKER, shuwa_worker).
-define(DEFAULT_TASK, default_task).
-define(TAKS_DB, shuwa_base_task).

-define(CHILD(I, Type, Args), {I, {I, start_link, Args}, permanent, 5000, Type, [I]}).

-define(CHILD2(I, Mod, Type, Args), {I, {Mod, start_link, Args}, permanent, 5000, Type, [Mod]}).

-define(TASK_NAME(Name), shuwa_utils:to_atom(lists:concat([shuwa_utils:to_atom(Name), "task"]))).
# Responding to Outside Changes

A recurring question in any AI implementation is how to handle outside changes - how to make the entity react upon being hit while it's patrolling, for example.

## Selector and Dynamic Selector

The `Selector` and `DynamicSelector` nodes are similar in function: they iterate through their children, running a child if it returns `RUNNING` or `SUCCESS`, but moving on and trying the next child if the original child returns `FAILURE`.
This allows the tree to prioritize some branches over others.

The obvious use case is something like this, for an imaginary worker which can fight/run from danger, has to eat, and otherwise works on a job:

```yaml
selector: [ensure-safe, ensure-not-hungry, work]
```

Where `ensure-safe` returns FAILURE if it doesn't make sense for it to run (entity is safe already), the selector moves on to `ensure-not-hungry`, etc.

The differences between Selector and Dynamic Selector are in the way they handle nodes that have previously returned `FAILURE`:

- plain ol' `Selector` moves on and forgets about it - not re-evaluating it until the control flow of the tree reaches the selector again at some point in the future.
  This can be useful in certain Sequence-y situations, but can result in 'stubbornness' if used in conditional / branching logic trees, such as the example above.
- `DynamicSelector` instead re-evaluates every child that has returned `FAILURE` on every tick, ensuring if something changes (e.g. the worker in the example above becomes hungry), the appropriate action is taken instantly.

## ConditionAction

In order to branch behavior, we need a good way to tell whether something of interest is happening to the Entity.
This is where `ConditionAction` steps in.

ConditionAction is a leaf node - it doesn't have any children.
Its `name` for tree prefabs is `condition`.
Its parameters allow you to check for 3 things:

- `componentPresent` specifies a Component which needs to be present on the Entity in order for the condition to hold
- `componentAbsent` (you guessed it) specifies a Component which needs to be absent from the Entity in order for the condition to hold
- `values` - specifies an array of conditions that need to be true on the fields of the Component for the condition to hold.
  - the format is: `[<condition-1>, <condition-2>, .. <condition-n>]` where every condition is of this format: `type fieldName operator valueToCheckAgainst`
  - The first parameter - `type` - has the following options:
    - `V` - [V]alue - denotes the `valueToCheckAgainst` parameter is a plain value
    - `F` - [F]ield - denotes the `valueToCheckAgainst` parameter is the name of another field in the component
    - `N` - [N]one - denotes no `valueToCheckAgainst` is specified - useful in checks where a second value wouldn't be meaningful.
  - Different data types support different operations specified by the `operator`:
    - `< > <= >= = == != !` - numbers (float, int, double, long, short, etc.)
    - `== != = !` - Strings and booleans
    - `!` and `=` is just shorthand notation for `!=` and `==` respectively.
    - `empty`, `nonEmpty`, `null` and `exists`: for use on nullable Objects (the `empty` checks are Collection specific)
  - some examples:
    - `["F currentHealth < maxHealth"]` checks whether the currentHealth field is under maxHealth, e.g. the entity is damaged.
    - `["V isEnraged = false", "V enrageCooldown = 0"]` checks preconditions for a fictional 'Enrage' ability the actor might want to do.
    - `["N enemiesInRange nonEmpty"]` - checks whether at least one enemy is in range (assuming the `enemiesInRange` field is a Collection)

### Example usage:

A `condition` will typically be used in a `sequence`.
In a sequence, if placed before some action nodes, it can ensure certain conditions were met before executing a further action.
Placing this encompassing `sequence` in a `dynamic` selector leads to easy branching behavior - the `condition` at the beginning of the sequence tells the tree whether to go further into the sequence or not.

The below example shows a simple tree which checks whether the entity's health is under 15, if so, logs a message, otherwise moves on - in this case to a `running` node.

```yaml
{
  dynamic:
    [
      {
        sequence:
          [
            {
              condition:
                {
                  componentPresent: "Core:Health",
                  values: ["V currentHealth < 15"],
                },
            },
            { log: { message: "I'm injured!" } },
          ],
      },
      running,
    ],
}
```

### Guard

An special version of a `condition` is `guard`. Guard functions like a condition, but it is a Decorator, and the underlying condition logic decides whether the child gets run or not.
This can be handy for clarity with simpler branches - if you don't want `sequence`s for every little condition check.

#### Example usage:

A guard can be used on its own in a selector. This is the same example as the above, but using a `guard`.

```yaml
{
  dynamic:
    [
      {
        guard:
          {
            componentPresent: "Core:Health",
            values: ["V currentHealth < 15"],
            child: { log: { message: "I'm injured!" } },
          },
      },
      running,
    ],
}
```

Keep in mind that the `child` can be anything - an `action`, another `decorator`, a `lookup` node or even a whole complex tree.

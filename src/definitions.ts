export interface JPushPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
